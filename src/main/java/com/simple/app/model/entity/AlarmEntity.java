package com.simple.app.model.entity;

import com.simple.app.model.AlarmArgs;
import com.simple.app.model.AlarmType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "\"alarm\"", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")
})
@SQLDelete(sql = "UPDATE \"alarm\" SET deleted_at = NOW() where id = ?")
@Where(clause = "deleted_at IS NULL")
public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 알람을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Convert(converter = AlarmArgsConverter.class)
    @Column(name = "alarm_args", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private AlarmArgs alarmArgs;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static AlarmEntity of(UserEntity userEntity, AlarmType alarmType, AlarmArgs alarmArgs) {
        AlarmEntity alarmEntity = new AlarmEntity();
        alarmEntity.setUser(userEntity);
        alarmEntity.setAlarmType(alarmType);
        alarmEntity.setAlarmArgs(alarmArgs);
        return alarmEntity;
    }
}
