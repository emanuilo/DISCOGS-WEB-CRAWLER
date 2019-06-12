package db;

import org.hibernate.Session;

@FunctionalInterface
public interface TransactionalCode<T> {

	public T run(final Session session);
}
