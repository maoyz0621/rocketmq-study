package me.jollyfly.rocketmq.starter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Data
@AllArgsConstructor
@ToString
public class Entity {

    private String id;

    private Boolean fail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return new EqualsBuilder()
                .append(fail, entity.fail)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fail)
                .toHashCode();
    }
}
