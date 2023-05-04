package com.troublefixer.vo;

public enum RoleName {
    ADMIN(2, "管理员"),
    MANAGER(1, "管理员"),
    NOMAL(0, "普通用户");

    private Integer role;
    private String roleName;

    RoleName(Integer role, String roleName){
        this.role = role;
        this.roleName = roleName;
    }

    public Integer getRole(){
        return role;
    }

    public String getRoleName(){
        return roleName;
    }
}
