package com.example.employeemanagementsystem.Controller;

import com.example.employeemanagementsystem.Api.ApiResponse;
import com.example.employeemanagementsystem.Model.Employee;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {
    ArrayList<Employee> employees = new ArrayList<>();

    @GetMapping("/get")
    public ResponseEntity<?> getEmployees(){
        return ResponseEntity.status(200).body(employees);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@RequestBody @Valid Employee employee, Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        employees.add(employee);
        return ResponseEntity.status(200).body(new ApiResponse(" Employee "+ employee.getName() + " has been added with ID "+ employee.getId()));
    }

    @PutMapping("/update/{index}")
    public ResponseEntity<?> updateEmployee(@RequestBody @Valid Employee employee,@PathVariable int index,Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        employees.set(index, employee);
        return ResponseEntity.status(200).body(new ApiResponse("Employee has been updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String id){
        for(Employee e: employees){
            if(e.getId().equals(id)){
                employees.remove(e);
                return ResponseEntity.status(200).body(new ApiResponse("Employee with id "+ id + " has been removed"));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("Could not find employee with id " + id));// verify if employee exists
    }

    @GetMapping("/search-by-position/{position}")
    public ResponseEntity<?> searchByPosition(@PathVariable String position){
        ArrayList<Employee> employeesByPositon = new ArrayList<>();

        if (!position.equalsIgnoreCase("supervisor") && !position.equalsIgnoreCase("Coordinator")){
            return ResponseEntity.status(400).body(new ApiResponse("Position entered must be either supervisor or coordinator"));
        }
        for (Employee e: employees){
            if(e.getPosition().equalsIgnoreCase(position)){
                employeesByPositon.add(e);
            }
        }
        if(employeesByPositon.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("Could not find employees with positon: "+ position));
        }
        return ResponseEntity.status(200).body(employeesByPositon);
    }


    @GetMapping("/get-between-ages/{minAge}/{maxAge}")
    public ResponseEntity<?> usersBetweenGivenAges(@PathVariable int minAge, @PathVariable int maxAge){
        ArrayList<Employee> usersBetweenAges = new ArrayList<>();

        if(minAge <= 0 || maxAge <= 0){
            return ResponseEntity.status(400).body(new ApiResponse("Age must be a positive number, entered ages: "+ minAge +" , " +maxAge));
        }
        if (minAge <=25){
            return ResponseEntity.status(400).body(new ApiResponse("minAge must be above 25"));
        }
        if(minAge >= maxAge){
            return ResponseEntity.status(400).body(new ApiResponse("minAge cannot be less than maxAge"));
        }

        for (Employee e: employees){
            if(e.getAge() >=minAge && e.getAge() <= maxAge){
                usersBetweenAges.add(e);
            }
        }

        if (usersBetweenAges.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("Couldn't find Users between ages " +minAge +"-"+maxAge));
        }
        return ResponseEntity.status(200).body(usersBetweenAges);
    }

    @PutMapping("/apply-for-annual-leave/{id}")
    public ResponseEntity<?> applyForAnnualLeave(@PathVariable String id){
        for (Employee e:employees){
            if(e.getId().equals(id)){
                if (e.isOnLeave() ){
                    return ResponseEntity.status(400).body(new ApiResponse("Employee must not be on leave"));
                }
                if(e.getAnnualLeave() <= 0){
                    return ResponseEntity.status(400).body(new ApiResponse("Employee have at lease 1 annual leave day"));
                }
                e.setOnLeave(true);
                e.setAnnualLeave(e.getAnnualLeave() - 1);
                return ResponseEntity.status(200).body(new ApiResponse("Employee with ID: "+ e.getId() +" has applied for annual leave, remaining annual leave days: "+ e.getAnnualLeave()));
            }
        }

        return ResponseEntity.status(400).body(new ApiResponse("Could not find employee with ID: " + id));
    }


    @GetMapping("/get-no-annual-leave")
    public ResponseEntity<?> employeesWithZeroAnnualLeave(){
        ArrayList<Employee> employeesWithNoAnnual = new ArrayList<>();

        for (Employee e : employees){
            if(e.getAnnualLeave() == 0){
                employeesWithNoAnnual.add(e);
            }
        }

        if (employeesWithNoAnnual.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("Could not find employees with zero annual leave:"));
        }

        return ResponseEntity.status(200).body(employeesWithNoAnnual);
    }

    @PutMapping("/promote/{promoterId}/{employeeId}")
    public ResponseEntity<?> promoteEmployee(@PathVariable String promoterId, @PathVariable String employeeId){

        for (Employee ePromoter : employees){
            if(ePromoter.getId().equals(promoterId)){
                if (!ePromoter.getPosition().equalsIgnoreCase("supervisor")){
                    return ResponseEntity.status(400).body(new ApiResponse("Requester is not a supervisor"));
                }
                for (Employee e: employees){
                    if(e.getId().equalsIgnoreCase(employeeId)){
                       if(e.getId().equals(ePromoter.getId())){
                           return ResponseEntity.status(400).body(new ApiResponse("Promoter cannot promote the promoter"));
                       }
                       if(e.getAge() < 30){
                           return ResponseEntity.status(400).body(new ApiResponse("Employee must be at least 30 years old. "));
                       }
                       if (e.isOnLeave()){
                           return ResponseEntity.status(400).body(new ApiResponse("Employee must not be on leave"));
                       }
                       if(e.getPosition().equalsIgnoreCase("supervisor")){
                           return ResponseEntity.status(400).body(new ApiResponse("Employee is already a supervisor"));
                       }
                       e.setPosition("Supervisor");
                        return ResponseEntity.status(200).body(new ApiResponse("Employee with id: "+ employeeId + " has been promoted"));
                    }
                }
                //did not find employee
                return ResponseEntity.status(400).body(new ApiResponse("Could not find employee with ID: " + employeeId +" to promote"));
            }
        }
        //did not find employee
        return ResponseEntity.status(400).body(new ApiResponse("Could not find requester with ID: " + promoterId));
    }


}
