package com.simple.app.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmArgs {
    // 알람을 발생시킨 사람
    private Integer fromUserId;
    private Integer targetId;
}
