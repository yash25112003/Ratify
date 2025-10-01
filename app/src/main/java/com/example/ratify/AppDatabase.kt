package com.example.ratify

import android.content.Context
import androidx.room.*

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "heart_rate")
    val heartRate: Float,

    @ColumnInfo(name = "respiratory_rate")
    val respiratoryRate: Float,

    val nausea: Float,
    val headache: Float,
    val diarrhea: Float,

    @ColumnInfo(name = "sore_throat")
    val soreThroat: Float,

    val fever: Float,

    @ColumnInfo(name = "muscle_ache")
    val muscleAche: Float,

    @ColumnInfo(name = "loss_of_smell_or_taste")
    val lossOfSmellOrTaste: Float,

    val cough: Float,

    @ColumnInfo(name = "shortness_of_breath")
    val shortnessOfBreath: Float,

    @ColumnInfo(name = "feeling_tired")
    val feelingTired: Float
)

@Dao
interface HealthRecordDao {
    @Insert
    suspend fun insert(record: HealthRecord)

    @Query("SELECT * FROM health_records")
    suspend fun getAll(): List<HealthRecord>

    @Query("DELETE FROM health_records")
    suspend fun deleteAll()
}

@Database(entities = [HealthRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthRecordDao(): HealthRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}