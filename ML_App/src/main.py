import psycopg2 as postgres
import pandas as pd
import enum
from sklearn.cluster import KMeans
import numpy as np
import math

def make_query(args):
    sub_queries = []
    for arg in args:
        if arg == InputType.Genre:
            sub_queries.append("""(select string_agg(genre.name, ';')
                        from album_genre
                        join genre on genre.id = album_genre.genre_id
                        where album_genre.album_id = a.id
                        group by album_genre.album_id) as genres""")
        elif arg == InputType.Style:
            sub_queries.append("""(select string_agg(style.name, ';')
                        from album_style
                        join style on style.id = album_style.style_id
                        where album_style.album_id = a.id
                        group by album_style.album_id) as styles""")
        elif arg == InputType.Year:
            sub_queries.append("""a.released as year""")

    query = "select " + ','.join(sub_queries) + " from album as a"
    if InputType.Year in args:
        query += " where a.released is not null"
    return query

class InputType(enum.Enum):
    Genre = 'genres'
    Style = 'styles'
    Year = 'year'

def valuesSet(results, type):
    values_set = set()
    results = results[type.value]
    for result in results:
        if result:
            for value in result.split(";"):
                values_set.add(value)
    return list(values_set)

def getFeatureVectors(results, input_args):
    featureVectors = []
    if InputType.Genre in input_args:
        genres_set = valuesSet(results, InputType.Genre)
    if InputType.Style in input_args:
        styles_set = valuesSet(results, InputType.Style)

    for result in results.values:
        featureVector = []
        if InputType.Genre in input_args:
            featureVector_temp = [0 for genre in genres_set]
            album_genres = result[results.keys().get_loc('genres')]
            if album_genres:
                album_genres = album_genres.split(";")
                for genre in album_genres:
                    featureVector_temp[genres_set.index(genre)] = 1
            featureVector += featureVector_temp

        if InputType.Style in input_args:
            featureVector_temp = [0 for style in styles_set]
            album_styles = result[results.keys().get_loc('styles')]
            if album_styles:
                album_styles = album_styles.split(";")
                for style in album_styles:
                    featureVector_temp[styles_set.index(style)] = 1
            featureVector += featureVector_temp

        if InputType.Year in input_args:
            album_year = result[results.keys().get_loc('year')]
            if math.isnan(album_year):
                featureVector.append(0)
            else:
                featureVector.append(int(album_year))

        featureVectors.append(featureVector)
    return featureVectors



# MAIN

print("--------------------------")
print("Izabrati algoritam: ")
print("--------------------------")
print("1. K Means")
print("2. Neki drugi")
print("3. Kraj")
print("--------------------------")
user_action = input("Izbor: ")

if user_action == "1":
    print("--------------------------")
    print("Izabrati ulazne podatke: ")
    print("--------------------------")
    print("1. Zanr")
    print("2. Stil")
    print("3. Godina izdanja")
    print("4. Zavrsi izbor")
    print("--------------------------")
    input_args = set()
    while(1):
        input_data_choice = input("Izbor: ")
        if input_data_choice == "1":
            input_args.add(InputType.Genre)
        elif input_data_choice == "2":
            input_args.add(InputType.Style)
        elif input_data_choice == "3":
            input_args.add(InputType.Year)
        else:
            break

    print("--------------------------")
    print("Uneti broj klastera: ")
    print("--------------------------")
    num_of_clusters = input("Unos: ")

    connection = postgres.connect(user="postgres",
                                  password="root",
                                  host="localhost",
                                  port="5432",
                                  database="crawler_db")
    query = make_query(input_args)
    results = pd.read_sql(query, connection)
    featureVectors = getFeatureVectors(results, input_args)

    X = np.array(featureVectors)
    kmeans = KMeans(n_clusters=int(num_of_clusters), random_state=0).fit(X)
    kmeans_result = kmeans.predict(X)
    kmeans_result = kmeans_result.tolist()

    output_strings = []
    for album_data, kmeans_cluster in zip(results.values, kmeans_result):
        output_strings.append('    ' + str(kmeans_cluster) + '      ' + str(album_data))

    # sort by a cluster
    output_strings.sort()
    for str in output_strings:
        print(str)

