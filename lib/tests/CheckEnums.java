import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;


public class CheckEnums {
    @Test
    void check() {
        checkDuplicatesInEnum(Events.class, Events.values());
        checkDuplicatesInEnum(Fields.class, Fields.values());
        checkDuplicatesInEnum(Operations.class, Operations.values());
        checkDuplicatesInEnum(ErrorCodes.class, ErrorCodes.values());
    }

    @TestOnly
    private void checkDuplicatesInEnum(Class<?> inClass, Enum<?>[] values) {
        for (int j = 0; j < values.length; j++)
            for (int k = j + 1; k < values.length; k++)
                if (k != j && Objects.equals(values[k].toString(), values[j].toString()))
                    Assertions.fail(values[k].name() + " and " + values[j].name() + " have the same val in " + inClass);
    }
}
