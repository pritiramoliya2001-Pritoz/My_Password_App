import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val accountType: String,
    val username: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) // Important for ByteArray
    val encryptedPassword: ByteArray
)
