package dao;

import entity.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {

    public void insertEmployee(Employee employee) {
        String sql = "INSERT INTO employees (name, email, department, salary) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getDepartment());
            ps.setInt(4, employee.getSalary());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Employee> selectEmployees(List<Employee> searchList) {
        List<Employee> employees = new ArrayList<>();

        if (searchList == null || searchList.isEmpty()) {
            return employees;
        }

        StringBuilder query = new StringBuilder("SELECT * FROM employees WHERE (");
        List<Object> params = new ArrayList<>();

        List<String> conditions = new ArrayList<>();
        for (Employee searchRow : searchList) {
            List<String> rowConditions = new ArrayList<>();

            if (searchRow.getDepartment() != null && !searchRow.getDepartment().isEmpty()) {
                rowConditions.add("department = ?");
                params.add(searchRow.getDepartment());
            }

            if (searchRow.getEmail() != null && !searchRow.getEmail().isEmpty()) {
                rowConditions.add("email LIKE ?");
                params.add(searchRow.getEmail() + "%");
            }

            if (searchRow.getMinSalary() >= 0 && searchRow.getMaxSalary() > 0) {
                rowConditions.add("salary BETWEEN ? AND ?");
                params.add(searchRow.getMinSalary());
                params.add(searchRow.getMaxSalary());
            }

            if (!rowConditions.isEmpty()) {
                conditions.add("(" + String.join(" AND ", rowConditions) + ")");
            }
        }

        if (conditions.isEmpty()) {
            return employees;
        }

        query.append(String.join(" OR ", conditions)).append(")");

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {

            int index = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    preparedStatement.setString(index++, (String) param);
                } else if (param instanceof Integer) {
                    preparedStatement.setInt(index++, (Integer) param);
                }
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Employee emp = new Employee();
                    emp.setId(resultSet.getInt("id"));
                    emp.setName(resultSet.getString("name"));
                    emp.setEmail(resultSet.getString("email"));
                    emp.setDepartment(resultSet.getString("department"));
                    emp.setSalary(resultSet.getInt("salary"));
                    employees.add(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

}
