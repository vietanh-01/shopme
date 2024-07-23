package com.shopme.admin.setting;

import com.shopme.common.entity.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Setting;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {
    List<Setting> findByCategory(SettingCategory category);
}
