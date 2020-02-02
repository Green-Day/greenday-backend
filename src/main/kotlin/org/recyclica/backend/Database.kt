package org.recyclica.backend

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

fun Table.textField(field: RecyclicaField) = varchar(field.fieldName, field.max)

val DATABASE_TABLES = arrayOf(Pictures, Groups, Users, UserGroups, UserTokens)

object Pictures : IntIdTable("pictures") // todo че-то доделать

class Picture(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Picture>(Pictures)
}

object Groups : IntIdTable("groups") {
    // todo набросать permission'ы
}

class Group(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Group>(Groups)
}

object Users : IntIdTable("users") {
    val avatar = reference("avatar", Pictures).nullable()
    val username = textField(RecyclicaField.USERNAME).uniqueIndex()
    val fullname = textField(RecyclicaField.FULLNAME).uniqueIndex()
    val passwordScrypt = binary("password_scrypt", 64)
    val passwordSalt = binary("password_salt", 8)
    val registeredOn = datetime("registered_on")
    val phone = textField(RecyclicaField.PHONE).nullable().uniqueIndex()
    val email = textField(RecyclicaField.EMAIL).nullable().uniqueIndex()
    val reputation = integer("reputation").default(0)
    val points = integer("points").default(0)

    val lockedColumns = listOf(passwordScrypt, passwordSalt)
}

object UserGroups : Table("user_groups") { // intermediate table
    val user = reference("user", Users)
    val group = reference("group", Groups)

    override val primaryKey = PrimaryKey(user, group)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var avatar by Picture optionalReferencedOn Users.avatar
    var username by Users.username
    var fullname by Users.fullname
    var passwordScrypt by Users.passwordScrypt
    var passwordSalt by Users.passwordSalt
    var registeredOn by Users.registeredOn
    var phone by Users.phone
    var email by Users.email
    var groups by Group via UserGroups
    var reputation by Users.reputation
    var points by Users.points
}


object UserTokens : IntIdTable("user_tokens") {
    val token = textField(RecyclicaField.TOKEN)
    val updatedOn = datetime("updated_on")
    val user = reference("user", Users)
}

class UserToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserToken>(UserTokens)

    var token by UserTokens.token
    var updateOn by UserTokens.updatedOn
    var user by User referencedOn UserTokens.user
}