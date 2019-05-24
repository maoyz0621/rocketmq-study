package me.jollyfly.rocketmq.starter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class MyBean {

    private String name;

    private Integer age;

    private String gender;

    private Integer quantity;

    private List<Entity> entities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MyBean myBean = (MyBean) o;

        return new EqualsBuilder()
                .append(name, myBean.name)
                .append(age, myBean.age)
                .append(gender, myBean.gender)
                .append(entities, myBean.entities)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(age)
                .append(gender)
                .append(entities)
                .toHashCode();
    }

    public String key() {
        return this.name + this.age + this.gender;
    }
}
