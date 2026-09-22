package io.edusoliveira.meudia.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.edusoliveira.meudia.data.db.dao.MonthBillEntryDao
import io.edusoliveira.meudia.data.db.dao.MonthlyIncomeDao
import io.edusoliveira.meudia.data.db.dao.RecurringBillDao
import io.edusoliveira.meudia.data.db.dao.TransactionDao
import io.edusoliveira.meudia.data.db.entities.MonthBillEntryEntity
import io.edusoliveira.meudia.data.db.entities.MonthlyIncomeEntity
import io.edusoliveira.meudia.data.db.entities.RecurringBillEntity
import io.edusoliveira.meudia.data.db.entities.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        RecurringBillEntity::class,
        MonthBillEntryEntity::class,
        MonthlyIncomeEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun recurringBillDao(): RecurringBillDao
    abstract fun monthBillEntryDao(): MonthBillEntryDao
    abstract fun monthlyIncomeDao(): MonthlyIncomeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Adiciona o módulo financeiro completo sem tocar na tabela `transactions`
        // já existente, para não perder o histórico de quem já usava o app.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recurring_bills` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `nome` TEXT NOT NULL,
                        `categoria` TEXT NOT NULL,
                        `valorPadrao` INTEGER NOT NULL,
                        `tipo` TEXT NOT NULL,
                        `diaVencimento` INTEGER NOT NULL,
                        `mesVencimento` INTEGER,
                        `ativa` INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `month_bill_entries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `recurringBillId` INTEGER,
                        `mesAno` TEXT NOT NULL,
                        `nome` TEXT NOT NULL,
                        `valor` INTEGER NOT NULL,
                        `categoria` TEXT NOT NULL,
                        `pago` INTEGER NOT NULL DEFAULT 0,
                        `dataPagamento` INTEGER,
                        FOREIGN KEY(`recurringBillId`) REFERENCES `recurring_bills`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_month_bill_entries_recurringBillId` ON `month_bill_entries` (`recurringBillId`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_month_bill_entries_mesAno` ON `month_bill_entries` (`mesAno`)",
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `monthly_incomes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `mesAno` TEXT NOT NULL,
                        `descricao` TEXT NOT NULL,
                        `valor` INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_monthly_incomes_mesAno` ON `monthly_incomes` (`mesAno`)",
                )
            }
        }

        // Adiciona suporte a ícone/foto customizados por conta, sem apagar nada.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `recurring_bills` ADD COLUMN `iconKey` TEXT")
                db.execSQL("ALTER TABLE `recurring_bills` ADD COLUMN `photoPath` TEXT")
                db.execSQL("ALTER TABLE `month_bill_entries` ADD COLUMN `iconKey` TEXT")
                db.execSQL("ALTER TABLE `month_bill_entries` ADD COLUMN `photoPath` TEXT")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meudia.db",
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { INSTANCE = it }
            }
        }
    }
}
