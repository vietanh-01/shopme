package com.shopme.admin.setting;

import com.shopme.common.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.setting.Setting;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {
    List<Setting> findByCategory(SettingCategory category);
}
