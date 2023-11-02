package android.example.myapplication002.screens.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * from user_table ORDER BY dateJoined DESC LIMIT 1")
    fun getLatestUser():User

    @Query("SELECT * from user_table WHERE userId= :key")
    suspend fun getUserById(key:Long):User

    @Query("SELECT userId from user_table ORDER BY dateJoined DESC LIMIT 1")
    fun getLatestUserId():Long

    @Query("SELECT * from user_table ORDER BY lastloggedIn DESC LIMIT 1")
    fun getLastLoggedInUser():User

    @Query("SELECT * from user_table WHERE firebaseUserId=:key")
    fun getUserByFirebaseId(key:String):User

    @Query("SELECT * FROM user_table WHERE userEmail=:key")
    fun getUserByEmail(key:String):User

    @Query("SELECT * from user_table WHERE userName=:key")
    fun getUserByUsername(key:String):User

    @Query("SELECT firebaseUserId FROM user_table ORDER BY dateJoined DESC")
    fun getAllFirebaseUserIds():MutableList<String>

    @Query("SELECT COUNT(userId) FROM user_table")
    fun getUsersCount():Int

    @Query("DELETE FROM user_table")
    suspend fun clear()

    @Query("SELECT userName FROM user_table ORDER BY dateJoined ASC")
    fun getAllUsernames():MutableList<String>

    @Query("SELECT userEmail FROM user_table")
    fun getAllEmails():List<String>

    @Query("SELECT passWord FROM user_table")
    fun getAllPasswords():List<String>

    @Query("SELECT password FROM user_table WHERE userEmail=:key")
    fun getPasswordOfEmail(key: String):String

    @Query("SELECT passWord FROM user_table WHERE userName=:key")
    fun getPasswordOfUsername(key:String):String

    @Query("SELECT * FROM user_table")
    fun getAllUsers():List<User>

    @Query("DELETE FROM user_table WHERE userId=:key")
    fun deleteUser(key:Long)

}