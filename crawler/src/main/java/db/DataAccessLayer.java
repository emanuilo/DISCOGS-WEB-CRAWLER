package db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataAccessLayer {

	private static final Logger LOG = LoggerFactory.getLogger(DataAccessLayer.class);

	private static SessionFactory sessionFactory;

	public static void initialize() throws Exception {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			StandardServiceRegistryBuilder.destroy(registry);
			throw e;
		}
	}

	public static <T> T transactional(final TransactionalCode<T> code) {	
		try (Session session = sessionFactory.openSession()) {
			Transaction t = session.beginTransaction();
			try {
				T result = code.run(session);
				t.commit();
				return result;
			} catch (Exception e) {
				if (t.isActive()) {
					t.rollback();
				}
				throw e;
			}
		} catch (Exception e) {
			LOG.error("Transactional execution failed", e);
			throw e;
		}
	}

	private DataAccessLayer() {
	}
}
