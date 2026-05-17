package app.devper.pharm.presentation.settings

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.repository.FakeProfileRepository
import app.devper.pharm.domain.repository.FakeSettingsRepository
import app.devper.pharm.domain.usecase.GetProfileUseCase
import app.devper.pharm.domain.usecase.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.UpdateSettingsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsEditorViewModelTest {

    private fun newVm(
        dispatchers: AppDispatchers,
        profileRepo: FakeProfileRepository = FakeProfileRepository(),
    ): SettingsEditorViewModel {
        val settingsRepo = FakeSettingsRepository()
        return SettingsEditorViewModel(
            settings = SettingsProvider(settingsRepo),
            refreshSettings = RefreshSettingsUseCase(settingsRepo, dispatchers),
            updateSettings = UpdateSettingsUseCase(settingsRepo, dispatchers),
            getProfile = GetProfileUseCase(profileRepo, dispatchers),
        )
    }

    @Test
    fun role_populates_after_get_profile_succeeds() = runVmTest { dispatchers ->
        val vm = newVm(dispatchers, FakeProfileRepository(
            initial = FakeProfileRepository.sampleUser.copy(role = Role.ADMIN),
        ))
        advanceUntilIdle()
        assertEquals(Role.ADMIN, vm.state.value.role)
    }

    @Test
    fun role_stays_unknown_on_profile_failure() = runVmTest { dispatchers ->
        val vm = newVm(dispatchers, FakeProfileRepository(getFailsWith = RuntimeException("boom")))
        advanceUntilIdle()
        assertEquals(Role.UNKNOWN, vm.state.value.role)
    }

    @Test
    fun toggle_group_collapses_and_expands() = runVmTest { dispatchers ->
        val vm = newVm(dispatchers)
        advanceUntilIdle()
        val initiallyCollapsed = vm.state.value.collapsedGroups.contains(SettingsMenuGroup.Compliance)
        assertTrue(initiallyCollapsed)

        vm.toggleGroup(SettingsMenuGroup.Compliance)
        assertFalse(SettingsMenuGroup.Compliance in vm.state.value.collapsedGroups)

        vm.toggleGroup(SettingsMenuGroup.Compliance)
        assertTrue(SettingsMenuGroup.Compliance in vm.state.value.collapsedGroups)
    }

    @Test
    fun toggle_group_can_collapse_another_group() = runVmTest { dispatchers ->
        val vm = newVm(dispatchers)
        advanceUntilIdle()
        vm.toggleGroup(SettingsMenuGroup.Reports)
        assertTrue(SettingsMenuGroup.Reports in vm.state.value.collapsedGroups)
        assertTrue(SettingsMenuGroup.Compliance in vm.state.value.collapsedGroups)
    }

    @Test
    fun menu_groups_for_admin_includes_users_and_bulk_import() = runVmTest { dispatchers ->
        val vm = newVm(dispatchers, FakeProfileRepository(
            initial = FakeProfileRepository.sampleUser.copy(role = Role.ADMIN),
        ))
        advanceUntilIdle()
        val keys = vm.state.value.menuGroups.flatMap { (_, items) -> items.map { it.key } }
        assertTrue(SettingsMenuKey.Users in keys)
        assertTrue(SettingsMenuKey.BulkImport in keys)
    }

    @Test
    fun menu_groups_for_user_excludes_users_and_bulk_import() = runVmTest { dispatchers ->
        val vm = newVm(dispatchers, FakeProfileRepository(
            initial = FakeProfileRepository.sampleUser.copy(role = Role.USER),
        ))
        advanceUntilIdle()
        val keys = vm.state.value.menuGroups.flatMap { (_, items) -> items.map { it.key } }
        assertFalse(SettingsMenuKey.Users in keys)
        assertFalse(SettingsMenuKey.BulkImport in keys)
    }
}
