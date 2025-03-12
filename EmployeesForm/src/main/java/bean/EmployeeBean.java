package bean;

import dao.EmployeeDao;
import entity.Employee;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped

public class EmployeeBean implements Serializable {

    private Employee newEmployee = new Employee();
    private List<Employee> searchList = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Employee> selectedSearchRows = new ArrayList<>();
    EmployeeDao employeeDao = new EmployeeDao();

    public EmployeeBean() {
        searchList.add(new Employee());
    }

    public void createEmployee() {
        if (newEmployee.getName() != null && !newEmployee.getName().trim().isEmpty()
                && newEmployee.getEmail() != null && !newEmployee.getEmail().trim().isEmpty()
                && newEmployee.getDepartment() != null && !newEmployee.getDepartment().trim().isEmpty()
                && newEmployee.getSalary() > 0) {
            employeeDao.insertEmployee(newEmployee);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Employee Created Successfully!"));
            newEmployee = new Employee();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "All fields are required!"));
        }
    }

    public void searchEmployees() {
        boolean hasDepartment = false;
        boolean hasSalaryRange = false; 
        boolean hasEmail = false; 

        for (Employee emp : searchList) {
            if (emp.getDepartment() != null && !emp.getDepartment().isEmpty()) {
                hasDepartment = true;
            }
            if (emp.getEmail() != null && !emp.getEmail().isEmpty()) {
                hasEmail = true;
            }
            if ( emp.getMinSalary() >= 0 && emp.getMaxSalary() > 0 && emp.getMinSalary() <= emp.getMaxSalary()) {
                hasSalaryRange = true;
            }
        }
        if (!hasDepartment && !hasEmail && !hasSalaryRange) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please enter at least one Department or a Salary range to search."));
            employees = new ArrayList<>();
            return;
        }
        employees = employeeDao.selectEmployees(searchList);

        if (employees != null && !employees.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Search results displayed."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No employees found matching your Data."));
        }
    }

    public void addSearchRow() {
        searchList.add(new Employee());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Row Added Successfully!"));
    }

    public void deleteSearchRows() {
        if (selectedSearchRows != null && !selectedSearchRows.isEmpty()) {
            searchList.removeAll(new ArrayList<>(selectedSearchRows));
            selectedSearchRows.clear();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Selected rows deleted."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Warning", "No rows selected!"));
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<Employee> searchList) {
        this.searchList = searchList;
    }

    public List<Employee> getSelectedSearchRows() {
        return selectedSearchRows;
    }

    public void setSelectedSearchRows(List<Employee> selectedSearchRows) {
        this.selectedSearchRows = selectedSearchRows;
    }

    public Employee getNewEmployee() {
        return newEmployee;
    }

    public void setNewEmployee(Employee newEmployee) {
        this.newEmployee = newEmployee;
    }

}
