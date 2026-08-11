package org.course.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class EmployeeDto {

    private int employeeId;
    private String lastName;
    private String firstName;
    private String title;
    private String titleOfCourtesy;
    private LocalDateTime birthDate;
    private LocalDateTime hireDate;
    private String address;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private String homePhone;
    private String extension;
    private byte[] photo;
    private String notes;
    private String photoPath;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("reportsToId")
    private Integer reportsToId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("reportsToName")
    private String reportsToName;

    public EmployeeDto() {}

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleOfCourtesy() { return titleOfCourtesy; }
    public void setTitleOfCourtesy(String titleOfCourtesy) { this.titleOfCourtesy = titleOfCourtesy; }

    public LocalDateTime getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }

    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getHomePhone() { return homePhone; }
    public void setHomePhone(String homePhone) { this.homePhone = homePhone; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public Integer getReportsToId() { return reportsToId; }
    public void setReportsToId(Integer reportsToId) { this.reportsToId = reportsToId; }

    public String getReportsToName() { return reportsToName; }
    public void setReportsToName(String reportsToName) { this.reportsToName = reportsToName; }
}
