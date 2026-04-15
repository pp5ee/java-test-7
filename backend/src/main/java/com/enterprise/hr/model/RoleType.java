package com.enterprise.hr.model;

public enum RoleType {
    CEO("CEO", "Chief Executive Officer", 1),
    CTO("CTO", "Chief Technology Officer", 2),
    CFO("CFO", "Chief Financial Officer", 2),
    COO("COO", "Chief Operating Officer", 2),
    MANAGER("MANAGER", "Department Manager", 3),
    TEAM_LEAD("TEAM_LEAD", "Team Lead", 4),
    SENIOR_DEVELOPER("SENIOR_DEVELOPER", "Senior Developer", 5),
    DEVELOPER("DEVELOPER", "Developer", 5),
    JUNIOR_DEVELOPER("JUNIOR_DEVELOPER", "Junior Developer", 6),
    HR_SPECIALIST("HR_SPECIALIST", "HR Specialist", 5),
    EMPLOYEE("EMPLOYEE", "Employee", 7);

    private final String name;
    private final String displayName;
    private final int level;

    RoleType(String name, String displayName, int level) {
        this.name = name;
        this.displayName = displayName;
        this.level = level;
    }

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public int getLevel() { return level; }

    public String getDescription() {
        return switch (this) {
            case CEO -> "Top executive with full system access";
            case CTO -> "Chief Technology Officer overseeing technical teams";
            case CFO -> "Chief Financial Officer overseeing finance";
            case COO -> "Chief Operating Officer overseeing operations";
            case MANAGER -> "Department manager with team oversight";
            case TEAM_LEAD -> "Team leader responsible for team coordination";
            case SENIOR_DEVELOPER -> "Experienced developer with mentoring responsibilities";
            case DEVELOPER -> "Full-stack or specialized software developer";
            case JUNIOR_DEVELOPER -> "Entry-level developer under mentorship";
            case HR_SPECIALIST -> "Human resources specialist";
            case EMPLOYEE -> "General employee";
        };
    }
}
