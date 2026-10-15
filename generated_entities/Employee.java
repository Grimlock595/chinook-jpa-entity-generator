package generated_entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", nullable = false)
    @NotNull
    private Integer employeeId;

    @Column(name = "last_name", length = 20, nullable = false)
    @NotNull
    @Size(max = 20)
    private String lastName;

    @Column(name = "first_name", length = 20, nullable = false)
    @NotNull
    @Size(max = 20)
    private String firstName;

    @Column(name = "title", length = 30, nullable = true)
    @Size(max = 30)
    private String title;

    @Column(name = "reports_to", nullable = true)
    private Integer reportsTo;

    @Column(name = "birth_date", nullable = true)
    private java.time.LocalDateTime birthDate;

    @Column(name = "hire_date", nullable = true)
    private java.time.LocalDateTime hireDate;

    @Column(name = "address", length = 70, nullable = true)
    @Size(max = 70)
    private String address;

    @Column(name = "city", length = 40, nullable = true)
    @Size(max = 40)
    private String city;

    @Column(name = "state", length = 40, nullable = true)
    @Size(max = 40)
    private String state;

    @Column(name = "country", length = 40, nullable = true)
    @Size(max = 40)
    private String country;

    @Column(name = "postal_code", length = 10, nullable = true)
    @Size(max = 10)
    private String postalCode;

    @Column(name = "phone", length = 24, nullable = true)
    @Size(max = 24)
    private String phone;

    @Column(name = "fax", length = 24, nullable = true)
    @Size(max = 24)
    private String fax;

    @Column(name = "email", length = 60, nullable = true)
    @Size(max = 60)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reports_to", referencedColumnName = "employee_id", insertable = false, updatable = false)
    private Employee reportsToEmployee;

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(Integer reportsTo) {
        this.reportsTo = reportsTo;
    }

    public java.time.LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(java.time.LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public java.time.LocalDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(java.time.LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Employee getReportsToEmployee() {
        return reportsToEmployee;
    }

    public void setReportsToEmployee(Employee reportsToEmployee) {
        this.reportsToEmployee = reportsToEmployee;
    }

}
