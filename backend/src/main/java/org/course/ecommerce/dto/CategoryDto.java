package org.course.ecommerce.dto;

public class CategoryDto {

    private Integer categoryID;
    private String categoryName;
    private String description;

    public CategoryDto() {}

    public CategoryDto(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public Integer getCategoryID() { return categoryID; }
    public void setCategoryID(Integer categoryID) { this.categoryID = categoryID; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
