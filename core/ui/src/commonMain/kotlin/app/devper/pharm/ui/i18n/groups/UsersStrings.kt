package app.devper.pharm.ui.i18n.groups

interface UsersStrings {
    val usersFormNotFound: String
    val usersFormLoadFailed: String
    val usersListLoadFailed: String
    val usersRoleChangeFailed: String
    val usersStatusChangeFailed: String
    val usersSetPasswordFailed: String
    val usersListSubtitle: String
    val usersSearchPlaceholder: String
    val usersAddCta: String
    val usersAddFirstCta: String
    val usersCountNoun: String
    val usersOwnAccountBadge: String
    val usersListEmpty: String
    val usersListNotFound: String
    val usersCannotEdit: String
    val usersHeaderName: String
    val usersHeaderUsername: String
    val usersHeaderRole: String
    val usersRoleSuper: String
    val usersRoleAdmin: String
    val usersRoleManager: String
    val usersRoleUser: String
    val usersRoleUnknown: String
    val usersStatusActive: String
    val usersStatusSuspended: String
    val usersActionChangeRole: String
    val usersActionSetPassword: String
    val usersActionSuspend: String
    val usersActionEnable: String
    val usersConfirmDeleteTitle: String
    val usersConfirmDeleteMessage: (String) -> String
    val usersConfirmRoleTitle: String
    val usersConfirmEnableTitle: String
    val usersConfirmSuspendTitle: String
    val usersConfirmEnableMessage: (String) -> String
    val usersConfirmSuspendMessage: (String) -> String
    val usersSetPasswordTitle: (String) -> String
    val usersFormAddTitle: String
    val usersFormEditTitle: String
    val usersFormInfoSection: String
    val usersFormUsername: String
    val usersFormPasswordCreate: String
    val usersFormPasswordNew: String
    val usersFormPasswordHint: String
}
