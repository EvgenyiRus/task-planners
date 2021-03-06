package com.tasklist.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@DynamicUpdate  // Hibernate будет генерировать инструкцию SQL каждый раз, когда обновляется сущность.
                // Этот сгенерированный SQL включает только измененные столбцы.(проблема с ресурсом,
                // т.к. необходимо следить за состоянием объекта каждый раз)
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Type(type = "org.hibernate.type.NumericBooleanType") // для автоматической конвертации чисел в true/false
    private boolean activated;

    @NotBlank
    // создается только 1 раз при добавлении (для активации пользователя)
    @Column(updatable = false)
    private String uuid;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
