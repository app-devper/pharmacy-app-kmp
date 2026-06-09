package app.devper.pharm.ui.i18n.groups

object UsersStringsTh : UsersStrings {
    override val usersListSubtitle = "บัญชีผู้ใช้ในระบบ User Management"
    override val usersSearchPlaceholder = "ค้นหาชื่อ / username / อีเมล…"
    override val usersAddCta = "เพิ่มผู้ใช้งาน"
    override val usersAddFirstCta = "เพิ่มผู้ใช้งานคนแรก"
    override val usersCountNoun = "คน"
    override val usersOwnAccountBadge = "บัญชีของคุณ"
    override val usersListEmpty = "ยังไม่มีผู้ใช้งาน"
    override val usersListNotFound = "ไม่พบผู้ใช้งานที่ค้นหา"
    override val usersCannotEdit = "ไม่สามารถแก้ไขได้"
    override val usersHeaderName = "ชื่อ-นามสกุล"
    override val usersStatusActive = "เปิดใช้งาน"
    override val usersStatusSuspended = "ระงับ"
    override val usersActionChangeRole = "เปลี่ยน Role"
    override val usersActionSetPassword = "ตั้งรหัสผ่าน"
    override val usersActionSuspend = "ระงับ"
    override val usersActionEnable = "เปิดใช้"
    override val usersConfirmDeleteTitle = "ยืนยันลบผู้ใช้งาน"
    override val usersConfirmDeleteMessage: (String) -> String = { name ->
        "ลบผู้ใช้งาน \"$name\" ?\nการดำเนินการนี้ไม่สามารถกู้คืนได้"
    }
    override val usersConfirmRoleTitle = "เปลี่ยน Role"
    override val usersConfirmEnableTitle = "ยืนยันเปิดใช้งาน"
    override val usersConfirmSuspendTitle = "ยืนยันระงับการใช้งาน"
    override val usersConfirmEnableMessage: (String) -> String = { name -> "เปิดใช้งานผู้ใช้ \"$name\"" }
    override val usersConfirmSuspendMessage: (String) -> String = { name -> "ระงับผู้ใช้ \"$name\"" }
    override val usersSetPasswordTitle: (String) -> String = { name -> "ตั้งรหัสผ่าน — $name" }
    override val usersFormAddTitle = "เพิ่มผู้ใช้งาน"
    override val usersFormEditTitle = "แก้ไขผู้ใช้งาน"
    override val usersFormInfoSection = "ข้อมูลผู้ใช้"
    override val usersFormUsername = "ชื่อผู้ใช้"
    override val usersFormPasswordCreate = "รหัสผ่าน (≥8 ตัว)"
    override val usersFormPasswordNew = "รหัสผ่านใหม่ (≥8 ตัว)"
    override val usersFormPasswordHint = "รหัสผ่านต้องไม่น้อยกว่า 8 ตัวอักษร"
}
