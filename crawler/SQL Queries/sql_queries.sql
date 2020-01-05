a) izlistati koliko zapisa pripada svakom od zanrova

SELECT count(album.name), genre.name
FROM album_genre
INNER JOIN album ON album.id = album_genre.album_id
INNER JOIN genre ON genre.id = album_genre.genre_id
GROUP BY genre.name
ORDER BY COUNT(album.name) DESC;



b) izlistati koliko zapisa pripada svakom od stilova

SELECT count(album.name), style.name
FROM album_style
INNER JOIN album ON album.id = album_style.album_id
INNER JOIN style ON style.id = album_style.style_id
GROUP BY style.name
ORDER BY COUNT(album.name) DESC;



c) prikazati rang listu prvih 20 albuma koji imaju najveci broj izdatih verzija (vise albuma
moze deliti jedno mesto na rang listi, pa konacan broj albuma na listi moze biti i veci
od 20)

SELECT album.name, album.versions
FROM album
ORDER BY album.versions DESC
LIMIT 20

SELECT album.name, count(album.name)
FROM album
GROUP BY album.name
ORDER BY COUNT(album.name) DESC
LIMIT 20



d) prikazati prvih 100 osoba koje imaju:
▪ najveci generalni rejting u pesmama (Credits)

SELECT artist.name, round(AVG(album.rating)::numeric, 2)
FROM credits
INNER JOIN album ON album.id = credits.album_id
INNER JOIN artist ON artist.id = credits.artist_id
GROUP BY artist.name
ORDER BY round(AVG(album.rating)::numeric, 2) DESC


▪ najvise ucesca kao vokal (Vocals)

SELECT artist.name, COUNT(album.name) 
FROM vocals
JOIN artist ON artist.id = vocals.artist_id
JOIN album ON album.id = vocals.album_id
GROUP BY artist.name
ORDER BY COUNT(album.name) DESC
limit 100


▪ najvise napisanih pesama – aranzman, reci teksta, muzika
(po kategorijama: Arranged by, Lyrics by, Music by)

select a.name, (select count(track.name)
				from music_by mb
				join track on track.id = mb.track_id
				where mb.artist_id = a.id) as music_col,
				
				(select count(track.name)
				from arranged_by ab
				join track on track.id = ab.track_id
				where ab.artist_id = a.id) as arranged_col,
				
				(select count(track.name)
				from lyrics_by lb
				join track on track.id = lb.track_id
				where lb.artist_id = a.id) as lyrics_col,
				
				((select count(track.name)
				from music_by mb
				join track on track.id = mb.track_id
				where mb.artist_id = a.id) + 
				(select count(track.name)
				from arranged_by ab
				join track on track.id = ab.track_id
				where ab.artist_id = a.id) + 
				(select count(track.name)
				from lyrics_by lb
				join track on track.id = lb.track_id
				where lb.artist_id = a.id)) as total
from artist a
order by total desc
limit 100



e) prikazati prvih 100 pesama koje se nalaze na najviše albuma, i osim broja albuma
(COUNT), uz svaku pesmu napisati podatke o tim albumima (Format, Country,
Year/Relased, Genre, Style)

select track.name, count(track.album_id)
from track
group by track.name
order by count(track.album_id) desc
limit 100



f) sve grupe i pojedinačne izvođače analiziranih pesama koje imaju veb sajt (popunjeno
polje Sites), u formatu: Naziv izvođača, Sajt

select a.name, a.website
from artist a
where a.website <> '' and 

//TODO ISKLJUCI FACEBOOK ITD


