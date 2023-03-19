package com.ecommerce.site.shop.repository;

import com.ecommerce.site.shop.model.entity.Setting;
import com.ecommerce.site.shop.model.enums.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory category);

    @Query("SELECT s FROM Setting s WHERE s.category = ?1 OR s.category = ?2")
    List<Setting> findByTwoCategories(SettingCategory catOne, SettingCategory catTwo);

    Optional<Setting> findByKey(String key);
}
