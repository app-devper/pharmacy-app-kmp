package app.devper.pharm.ui.i18n.groups

object UsersStringsEn : UsersStrings {
    override val usersListSubtitle = "User Management accounts"
    override val usersSearchPlaceholder = "Search name / username / email…"
    override val usersAddCta = "Add user"
    override val usersAddFirstCta = "Add the first user"
    override val usersCountNoun = "users"
    override val usersOwnAccountBadge = "Your account"
    override val usersListEmpty = "No users yet"
    override val usersListNotFound = "No users match the search"
    override val usersCannotEdit = "Cannot be edited"
    override val usersHeaderName = "Full name"
    override val usersStatusActive = "Active"
    override val usersStatusSuspended = "Suspended"
    override val usersActionChangeRole = "Change role"
    override val usersActionSetPassword = "Set password"
    override val usersActionSuspend = "Suspend"
    override val usersActionEnable = "Enable"
    override val usersConfirmDeleteTitle = "Confirm user deletion"
    override val usersConfirmDeleteMessage: (String) -> String = { name ->
        "Delete user \"$name\"?\nThis action cannot be undone."
    }
    override val usersConfirmRoleTitle = "Change role"
    override val usersConfirmEnableTitle = "Confirm enable"
    override val usersConfirmSuspendTitle = "Confirm suspension"
    override val usersConfirmEnableMessage: (String) -> String = { name -> "Enable user \"$name\"" }
    override val usersConfirmSuspendMessage: (String) -> String = { name -> "Suspend user \"$name\"" }
    override val usersSetPasswordTitle: (String) -> String = { name -> "Set password — $name" }
    override val usersFormAddTitle = "Add user"
    override val usersFormEditTitle = "Edit user"
    override val usersFormInfoSection = "User info"
    override val usersFormUsername = "Username"
    override val usersFormPasswordCreate = "Password (≥8 chars)"
    override val usersFormPasswordNew = "New password (≥8 chars)"
    override val usersFormPasswordHint = "Password must be at least 8 characters"
    override val usersRoleChangeFailed = "Failed to change role"
    override val usersStatusChangeFailed = "Failed to change status"
    override val usersSetPasswordFailed = "Failed to set password"
    override val usersFormNotFound = "User not found"
}
