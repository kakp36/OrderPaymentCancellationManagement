package service.support;

import java.time.LocalDateTime;

public class SystemTimeProvider implements TimeProvider {
    //現在の時間を取得する
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}