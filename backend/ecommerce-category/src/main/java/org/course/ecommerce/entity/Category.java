package org.course.ecommerce.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Categories", schema = "dbo")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private int categoryID;

    @Column(name = "CategoryName")
    private String categoryName;

    @Column(name = "Description")
    private String description;

    @Lob
    @Column(name = "Picture")
    private byte[] picture;

    public Category() {}

    public int getCategoryID() { return categoryID; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public byte[] getPicture() { return picture; }
    public void setPicture(byte[] picture) { this.picture = picture; }
}
