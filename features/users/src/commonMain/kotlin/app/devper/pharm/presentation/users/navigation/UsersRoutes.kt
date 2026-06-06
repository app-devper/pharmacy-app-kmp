package app.devper.pharm.presentation.users

import kotlinx.serialization.Serializable

@Serializable data object Users
@Serializable data object UserAdd
@Serializable data class UserEdit(val id: String)
