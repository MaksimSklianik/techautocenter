package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());

    // SQL для создания таблиц
    private static final String[] CREATE_TABLES_SQL = {
            // Таблица пользователей
            """
    CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT NOT NULL UNIQUE,
        password TEXT NOT NULL,
        full_name TEXT NOT NULL,
        phone TEXT,
        role TEXT NOT NULL,
        active BOOLEAN DEFAULT 1,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
    """,

            // Таблица записей сервиса
            """
    CREATE TABLE IF NOT EXISTS service_records (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        date DATE NOT NULL,
        client_name TEXT NOT NULL,
        client_phone TEXT NOT NULL,
        car_model TEXT NOT NULL,
        license_plate TEXT,
        service_type TEXT NOT NULL,
        description TEXT,
        cost REAL NOT NULL,
        status TEXT NOT NULL DEFAULT 'Новая',
        assigned_mechanic_id INTEGER,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (assigned_mechanic_id) REFERENCES users(id)
    )
    """,

            // Таблица запчастей
            """
    CREATE TABLE IF NOT EXISTS spare_parts (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        code TEXT NOT NULL UNIQUE,
        description TEXT,
        compatible_models TEXT,
        quantity INTEGER NOT NULL DEFAULT 0,
        price REAL NOT NULL,
        supplier TEXT,
        min_quantity INTEGER DEFAULT 5,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
    """,

            // Таблица расписания работ
            """
    CREATE TABLE IF NOT EXISTS work_schedules (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        mechanic_id INTEGER NOT NULL,
        record_id INTEGER NOT NULL,
        start_time TIMESTAMP NOT NULL,
        end_time TIMESTAMP NOT NULL,
        status TEXT NOT NULL DEFAULT 'Запланировано',
        notes TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (mechanic_id) REFERENCES users(id),
        FOREIGN KEY (record_id) REFERENCES service_records(id),
        CHECK (end_time > start_time)
    )
    """,

            // Таблица использования запчастей
            """
    CREATE TABLE IF NOT EXISTS parts_usage (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        record_id INTEGER NOT NULL,
        part_id INTEGER NOT NULL,
        quantity INTEGER NOT NULL DEFAULT 1,
        price_per_unit REAL NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (record_id) REFERENCES service_records(id),
        FOREIGN KEY (part_id) REFERENCES spare_parts(id),
        CHECK (quantity > 0)
    )
    """,

            // Таблица платежей
            """
    CREATE TABLE IF NOT EXISTS payments (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        payment_id TEXT NOT NULL UNIQUE,
        amount REAL NOT NULL,
        currency TEXT NOT NULL DEFAULT 'RUB',
        status TEXT NOT NULL,
        record_id INTEGER NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (record_id) REFERENCES service_records(id)
    )
    """,

            // Индексы для улучшения производительности
            """
    CREATE INDEX IF NOT EXISTS idx_service_records_status ON service_records(status)
    """,
            """
    CREATE INDEX IF NOT EXISTS idx_work_schedules_mechanic ON work_schedules(mechanic_id)
    """,
            """
    CREATE INDEX IF NOT EXISTS idx_work_schedules_record ON work_schedules(record_id)
    """
    };

    // Триггеры для обновления временных меток
    private static final String[] CREATE_TRIGGERS_SQL = {



    };

    // Индексы для улучшения производительности
    private static final String[] CREATE_INDEXES_SQL = {
            "CREATE INDEX IF NOT EXISTS idx_service_records_date ON service_records(date)",
            "CREATE INDEX IF NOT EXISTS idx_service_records_status ON service_records(status)",
            "CREATE INDEX IF NOT EXISTS idx_service_records_mechanic ON service_records(assigned_mechanic_id)",
            "CREATE INDEX IF NOT EXISTS idx_work_schedule_mechanic ON work_schedule(mechanic_id)",
            "CREATE INDEX IF NOT EXISTS idx_work_schedule_record ON work_schedule(record_id)",
            "CREATE INDEX IF NOT EXISTS idx_parts_usage_record ON parts_usage(record_id)",
            "CREATE INDEX IF NOT EXISTS idx_parts_usage_part ON parts_usage(part_id)"
    };

    // Начальные данные
    private static final String[] INITIAL_DATA_SQL = {
            // Администратор по умолчанию (пароль: admin)

            // Менеджер по умолчанию (пароль: manager)


            // Механик по умолчанию (пароль: mechanic)


            // Тестовые запчасти



            // Тестовые записи сервиса


    };

    /**
     * Инициализирует базу данных: создает таблицы, индексы, триггеры и начальные данные
     */
    public static void initialize() {
        logger.info("Инициализация базы данных...");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Включение поддержки внешних ключей
            stmt.execute("PRAGMA foreign_keys = ON");

            // Создание таблиц
            logger.info("Создание таблиц...");
            for (String sql : CREATE_TABLES_SQL) {
                stmt.execute(sql);
            }

            // Создание триггеров
            logger.info("Создание триггеров...");
            for (String sql : CREATE_TRIGGERS_SQL) {
                stmt.execute(sql);
            }

            // Создание индексов
            logger.info("Создание индексов...");
            for (String sql : CREATE_INDEXES_SQL) {
                stmt.execute(sql);
            }

            // Инициализация начальных данных
            logger.info("Добавление начальных данных...");
            for (String sql : INITIAL_DATA_SQL) {
                stmt.execute(sql);
            }

            logger.info("База данных успешно инициализирована");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка инициализации базы данных", e);
            throw new RuntimeException("Ошибка инициализации базы данных", e);
        }
    }

    /**
     * Возвращает соединение с базой данных
     * @return Connection объект соединения
     * @throws SQLException если произошла ошибка при подключении
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Регистрация драйвера (необходимо для некоторых версий JDBC)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC драйвер не найден", e);
        }

        // Получение URL базы данных из конфигурации
        String dbUrl = AppConfig.getDatabaseUrl();
        logger.log(Level.INFO, "Подключение к базе данных: {0}", dbUrl);

        // Настройка соединения
        Connection conn = DriverManager.getConnection(dbUrl);

        // Оптимизация для SQLite
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode = WAL");
            stmt.execute("PRAGMA synchronous = NORMAL");
            stmt.execute("PRAGMA temp_store = MEMORY");
            stmt.execute("PRAGMA cache_size = 10000");
        }

        return conn;
    }

    /**
     * Закрывает соединение с базой данных
     * @param conn соединение для закрытия
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.commit();
                }
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Ошибка при закрытии соединения с БД", e);
            }
        }
    }

    /**
     * Выполняет откат транзакции и закрывает соединение
     * @param conn соединение для отката
     */
    public static void rollbackConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Ошибка при откате транзакции", e);
            }
        }
    }
}