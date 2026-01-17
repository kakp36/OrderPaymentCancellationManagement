package service.support;

import java.time.LocalDateTime;

//時間取得を1か所にまとめる
public class TimeProvider {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
