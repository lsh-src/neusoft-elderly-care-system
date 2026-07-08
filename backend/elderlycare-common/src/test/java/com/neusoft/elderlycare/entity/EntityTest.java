package com.neusoft.elderlycare.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("实体类测试")
class EntityTest {

    // ==================== BaseEntity ====================

    @Test
    @DisplayName("BaseEntity 字段赋值与取值")
    void baseEntity_fields() {
        BaseEntity entity = new BaseEntity();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(100L);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setDeleted(0);

        assertEquals(100L, entity.getId());
        assertEquals(now, entity.getCreateTime());
        assertEquals(now, entity.getUpdateTime());
        assertEquals(0, entity.getDeleted());
    }

    @Test
    @DisplayName("BaseEntity 默认值应为 null")
    void baseEntity_defaults() {
        BaseEntity entity = new BaseEntity();

        assertNull(entity.getId());
        assertNull(entity.getCreateTime());
        assertNull(entity.getUpdateTime());
        assertNull(entity.getDeleted());
    }

    @Test
    @DisplayName("BaseEntity equals 应基于所有字段")
    void baseEntity_equals() {
        BaseEntity a = new BaseEntity();
        a.setId(1L);
        BaseEntity b = new BaseEntity();
        b.setId(1L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("BaseEntity 不同 id 应不相等")
    void baseEntity_notEquals() {
        BaseEntity a = new BaseEntity();
        a.setId(1L);
        BaseEntity b = new BaseEntity();
        b.setId(2L);

        assertNotEquals(a, b);
    }

    // ==================== SysUser ====================

    @Test
    @DisplayName("SysUser 应继承 BaseEntity 的所有字段")
    void sysUser_inheritsBase() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setCreateTime(LocalDateTime.now());
        user.setDeleted(0);

        assertEquals(1L, user.getId());
        assertNotNull(user.getCreateTime());
        assertEquals(0, user.getDeleted());
    }

    @Test
    @DisplayName("SysUser 业务字段赋值与取值")
    void sysUser_businessFields() {
        SysUser user = new SysUser();
        user.setPhone("13800000000");
        user.setPassword("encoded-password");
        user.setName("张三");
        user.setAge(68);
        user.setGender("男");
        user.setRole("ROLE_ADMIN");
        user.setEnabled(1);

        assertEquals("13800000000", user.getPhone());
        assertEquals("encoded-password", user.getPassword());
        assertEquals("张三", user.getName());
        assertEquals(68, user.getAge());
        assertEquals("男", user.getGender());
        assertEquals("ROLE_ADMIN", user.getRole());
        assertEquals(1, user.getEnabled());
    }

    @Test
    @DisplayName("SysUser 默认值应为 null")
    void sysUser_defaults() {
        SysUser user = new SysUser();

        assertNull(user.getPhone());
        assertNull(user.getPassword());
        assertNull(user.getName());
        assertNull(user.getAge());
        assertNull(user.getGender());
        assertNull(user.getRole());
        assertNull(user.getEnabled());
    }

    @Test
    @DisplayName("SysUser @ToString.Exclude 应排除 password")
    void sysUser_toStringExcludesPassword() {
        SysUser user = new SysUser();
        user.setPhone("13800000000");
        user.setPassword("secret-password");
        user.setName("张三");

        String str = user.toString();
        assertFalse(str.contains("secret-password"), "toString 不应包含密码明文");
        assertTrue(str.contains("13800000000"), "toString 应包含手机号");
        assertTrue(str.contains("张三"), "toString 应包含姓名");
    }

    @Test
    @DisplayName("SysUser equals 应基于所有字段（含继承）")
    void sysUser_equals() {
        SysUser a = new SysUser();
        a.setId(1L);
        a.setPhone("13800000000");
        a.setName("张三");

        SysUser b = new SysUser();
        b.setId(1L);
        b.setPhone("13800000000");
        b.setName("张三");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("SysUser 不同手机号应不相等")
    void sysUser_notEqualsDifferentPhone() {
        SysUser a = new SysUser();
        a.setId(1L);
        a.setPhone("13800000000");

        SysUser b = new SysUser();
        b.setId(1L);
        b.setPhone("13800000001");

        assertNotEquals(a, b);
    }
}
