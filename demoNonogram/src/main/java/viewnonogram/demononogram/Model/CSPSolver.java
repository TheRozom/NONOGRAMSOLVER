package viewnonogram.demononogram.Model;

import java.io.*;
import java.util.*;

public class CSPSolver {

    private int num_rows, num_cols;
    private Variable[] rows, cols;
    private Queue<Variable> variables;
    private List<String> solutions;

    public CSPSolver() {
        // Initialize the variables queue with a priority queue and a custom comparator
        this.variables = new PriorityQueue<>(new variable_comparator());
        this.solutions = new ArrayList<>();
    }

    public void solve_task() {
        try {
            // Load the task from input
            this.load_task();
        } catch (IOException exception) {
            System.out.println("IOException: " + exception.toString());
            System.exit(-1);
        }
        // Perform backtracking search to find solutions
        this.backtracking_search();
        // Print the found solutions
        this.print_solutions();
    }

    private void load_task() throws IOException {
        int line_number = 0;
        BufferedReader buf_read = new BufferedReader(new InputStreamReader(System.in));
        cycle: while(true) {
            String[] line = buf_read.readLine().split(",");
            if(line_number == 0) {
                // Read the number of rows and columns
                this.num_rows = Integer.parseInt(line[0]);
                this.num_cols = Integer.parseInt(line[1]);
                // Initialize the rows and columns arrays
                this.rows = new Variable[this.num_rows];
                this.cols = new Variable[this.num_cols];
            }
            if(line_number > 0 && line_number <= this.num_rows) {
                // Load constraints for each row
                int id = line_number - 1;
                this.rows[id] = new Variable(id, this.num_cols, true, false);
                for(int i = 0; i < line.length; i = i + 2) {
                    char color = line[i].charAt(0);
                    int length = Integer.parseInt(line[i + 1]);
                    this.rows[id].set_constraint(new Constraint(length, color));
                }
                this.rows[id].set_domain();
            }
            if(line_number > this.num_rows && line_number <= this.num_rows + this.num_cols) {
                // Load constraints for each column
                int id = line_number - this.num_rows - 1;
                this.cols[id] = new Variable(id, this.num_rows, false, true);
                for(int i = 0; i < line.length; i = i + 2) {
                    char color = line[i].charAt(0);
                    int length = Integer.parseInt(line[i + 1]);
                    this.cols[id].set_constraint(new Constraint(length, color));
                }
                this.cols[id].set_domain();
            }
            if(line_number++ >= this.num_rows + this.num_cols) {
                // Break the loop if all rows and columns are loaded
                break cycle;
            }
        }
    }

    private void backtracking_search() {
        // Start the recursive backtracking search
        this.recursive_backtracking(0);
    }

    private void recursive_backtracking(int step) {
        // Sort the variables based on the size of their domains
        this.sort_variables();
        if(this.variables.isEmpty()) {
            // If all variables are assigned, add the solution
            this.add_solution();
            return;
        }
        // Get the next variable to assign
        Variable var = this.variables.poll();
        for(Value val : var.get_domain()) {
            // Assign the value to the variable
            this.assign_value(var, val);
            if(this.forward_checking(var, step)) {
                // Perform forward checking and arc consistency checks
                this.arc_consistency(step);
                // Recursively continue the search
                this.recursive_backtracking(step + 1);
            }
            // Undo the assignment and remove any inconsistent values
            this.step_back(step);
            this.assign_value(var, null);
        }
    }

    private void sort_variables() {
        // Clear the variables queue
        this.variables.clear();
        // Add unassigned row variables to the queue
        for(Variable row : this.rows) {
            if(row.get_value() == null) {
                this.variables.add(row);
            }
        }
        // Add unassigned column variables to the queue
        for(Variable col : this.cols) {
            if(col.get_value() == null) {
                this.variables.add(col);
            }
        }
    }

    private void assign_value(Variable var, Value val) {
        if(var.is_row()) {
            // Assign the value to the row variable
            this.rows[var.get_id()].set_value(val);
        }
        if(var.is_col()) {
            // Assign the value to the column variable
            this.cols[var.get_id()].set_value(val);
        }
    }

    private boolean forward_checking(Variable var, int step) {
        if(var.is_row()) {
            Value val_1 = this.rows[var.get_id()].get_value();
            for(Variable col : this.cols) {
                if(col.get_value() == null) {
                    Iterator<Value> domain = col.get_domain().iterator();
                    while(domain.hasNext()) {
                        Value val_2 = domain.next();
                        // Check if the assigned row value is consistent with the column values
                        if(val_1.get()[col.get_id()] != val_2.get()[var.get_id()]) {
                            col.push_to_removed(val_2, step);
                            domain.remove();
                        }
                    }
                    // If the domain becomes empty, backtrack
                    if(col.get_domain().isEmpty()) {
                        return false;
                    }
                }
            }
        }
        if(var.is_col()) {
            Value val_1 = this.cols[var.get_id()].get_value();
            for(Variable row : this.rows) {
                if(row.get_value() == null) {
                    Iterator<Value> domain = row.get_domain().iterator();
                    while(domain.hasNext()) {
                        Value val_2 = domain.next();
                        // Check if the assigned column value is consistent with the row values
                        if(val_1.get()[row.get_id()] != val_2.get()[var.get_id()]) {
                            row.push_to_removed(val_2, step);
                            domain.remove();
                        }
                    }
                    // If the domain becomes empty, backtrack
                    if(row.get_domain().isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void arc_consistency(int step) {
        Stack<Arc> stack = new Stack<>();
        // Add all arcs between unassigned row and column variables to the stack
        for(Variable row : this.rows) {
            for (Variable col : this.cols) {
                if(row.get_value() == null && col.get_value() == null) {
                    stack.push(new Arc(row, col));
                    stack.push(new Arc(col, row));
                }
            }
        }
        while(!stack.isEmpty()) {
            Arc arc = stack.pop();
            if(arc.remove_inconsistent(step)) {
                if(arc.get_var1().is_row()) {
                    // If a row value is removed, add arcs from unassigned columns to the row
                    for(Variable col : this.cols) {
                        if(col.get_value() == null) {
                            stack.push(new Arc(col, arc.get_var1()));
                        }
                    }
                }
                if(arc.get_var1().is_col()) {
                    // If a column value is removed, add arcs from unassigned rows to the column
                    for(Variable row : this.rows) {
                        if(row.get_value() == null) {
                            stack.push(new Arc(row, arc.get_var1()));
                        }
                    }
                }
            }
        }
    }

    private void step_back(int step) {
        // Restore the removed values for columns at the current step
        for(Variable col : this.cols) {
            col.pop_from_removed(step);
        }
        // Restore the removed values for rows at the current step
        for(Variable row : this.rows) {
            row.pop_from_removed(step);
        }
    }

    private void add_solution() {
        // Build the solution string from the assigned row values
        String solution = new String();
        for(Variable row : this.rows) {
            solution += String.valueOf(row.get_value().get()) + "\n";
        }
        // Add the solution to the list of solutions
        this.solutions.add(solution);
    }

    public void print_solutions() {
        if(this.solutions.isEmpty()) {
            System.out.println("null");
        } else {
            // Print each solution
            for(String solution : this.solutions) {
                System.out.println(solution);   
            }
        }
    }

    private class variable_comparator implements Comparator<Variable> {
        @Override
        public int compare(Variable var_1, Variable var_2) {
            // Compare variables based on the size of their domains
            if(var_1.get_domain().size() > var_2.get_domain().size()) {
                return 1;
            }
            if(var_1.get_domain().size() < var_2.get_domain().size()) {
                return -1;
            }
            return 0;
        }
    }
}