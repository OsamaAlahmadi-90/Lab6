package com.example.employeemanagementsystem.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Employee {

    @NotEmpty(message = "ID cannot be empty")
    @Size(min = 3, message = "ID must be more than 2 characters")
    private String id;

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 5, message = "Name must be more than 4 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name should only contain characters")
    private String name;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email format is incorrect")
    private String email;

    @NotEmpty(message = "Phone number cannot be empty")
    @Pattern(regexp = "^05[0-9]+$", message = "Phone number must be digits and starts with 05 ")
    @Size(min = 10,max = 10, message = "Phone number must be 10 digits")
    private String phoneNumber;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be a positive number")
    @Min(value = 26, message = "Age must be more than 25.")
    private int age;

    @NotEmpty(message = "position cannot be empty")
    @Pattern(regexp = "(?i)^(Supervisor|Coordinator)$")
    private String position;

    @NotNull (message = "onLeave cannot be empty")
    @AssertFalse (message = "Initial onLeave value must be false.")
    private boolean onLeave;

    @NotNull (message = "hireDate cannot be empty")
    @PastOrPresent (message = "hireDate must be in the past or present")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    @NotNull(message = "Annual leave is required")
    @PositiveOrZero(message = "Annual leave must be a positive number")
    private int annualLeave;
}
