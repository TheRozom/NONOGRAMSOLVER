package viewnonogram.demononogram.Model;



import java.util.*;


public class Variable {

    private static final char NONE = '_';

    private int id, size;
    private boolean row, col;
    private Value assigned_value;
    private Queue<Value> domain;
    private Stack<Value> removed_values;
    private List<Constraint> constraints;
    private Map<Integer, Integer> track;
    

    Variable(int id, int size, boolean row, boolean col) {
        this.id = id;
        this.size = size;
        this.row = row;
        this.col = col;
        this.assigned_value = null;
        this.constraints = new ArrayList<>();
        this.domain = new PriorityQueue<>(new value_comparator());
        this.removed_values = new Stack<>();
        this.track = new HashMap<>();
    }

    public void push_to_removed(Value val, int step) {
        int count = this.track.containsKey(step) ? this.track.get(step) : 0;
        this.track.put(step, count + 1);
        this.removed_values.push(val);
    }

    public void pop_from_removed(int step) {
        if(this.track.containsKey(step)) {
            for(int i = 0; i < this.track.get(step); i++) {
                this.domain.add(this.removed_values.pop());
            }
            this.track.remove(step);
        }
    }

    public void set_constraint(Constraint constr) {
        this.constraints.add(constr);
    }

    public List<Constraint> get_constraints() {
        return this.constraints;
    }

    public void set_domain() {
        this.find_values(0,0, NONE, new char[this.size]);
    }
    public boolean isAssigned() {
        return assigned_value != null;
    }


    public Queue<Value> get_domain() {
        return this.domain;
    }

    public void print_domain() {
        for(Value value : this.domain) {
            System.out.println(value.get());
        }
    }

    public void set_value(Value val) {
        this.assigned_value = val;
    }

    public Value get_value() {
        return this.assigned_value;
    }

    private void find_values(int index, int constr_id, char prev, char[] val) {
        // Base case: If the current index is equal to the size of the row/column
        if (index == this.size) {
            // If all constraints have been processed
            if (constr_id == this.constraints.size()) {
                // Add a new Value object to the domain queue using a clone of the current val array
                this.domain.add(new Value(val.clone()));
            }
            // If there are constraints left to process, backtrack
            return;
        }

        // Recursive case: If the current index is within the bounds of the row/column size
        if (index < this.size) {
            // If there are constraints left to process
            if (constr_id < this.constraints.size()) {
                Constraint constr = this.constraints.get(constr_id);
                // If the constraint can be placed at the current index without violating the row/column size
                // and the previous cell's color is different from the current constraint's color (or the previous cell is empty)
                if (constr.get_length() + index <= this.size && (prev == NONE || (prev != NONE && prev != constr.get_color()))) {
                    // Fill the cells from the current index up to the length of the constraint with the constraint's color
                    for (int i = 0; i < constr.get_length(); i++) {
                        val[index + i] = constr.get_color();
                    }
                    // Recursively call find_values with the updated index, next constraint index, and current constraint's color
                    find_values(index + constr.get_length(), constr_id + 1, constr.get_color(), val);
                }
            }

            // If the current constraint cannot be placed at the current index
            // Set the current cell to NONE (empty)
            val[index] = NONE;
            // Recursively call find_values with the next index, same constraint index, and NONE as the previous color
            find_values(index + 1, constr_id, NONE, val);
        }
    }

    public int get_id() {
        return this.id;
    }

    public int get_size() {
        return this.size;
    }

    public boolean is_row() {
        return this.row;
    }

    public boolean is_col() {
        return this.col;
    }

    private class value_comparator implements Comparator<Value> {
        @Override
        public int compare(Value val_1, Value val_2) {
            if(val_1.get_rank() > val_2.get_rank()) {
                return 1;
            }
            if(val_1.get_rank() < val_2.get_rank()) {
                return -1;
            }
            return 0;
        }
    }

}
