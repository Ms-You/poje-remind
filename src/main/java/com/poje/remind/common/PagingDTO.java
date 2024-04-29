package com.poje.remind.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PagingDTO {
    private int page;   // 현재 페이지 번호
    private int size;   // 한 화면에 출력할 데이터 수
    private int pageNum;   // 페이지 번호 수
    private int limit;  // 화면에 표시되는 오브젝트의 시작

    public PagingDTO(int page) {
        this.page = page;
        this.size = 12;
        this.pageNum = 5;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int limitCalc() {
        // limit 시작 위치 계산
        return limit = (page - 1) * size;
    }
}
