package com.poje.remind.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PagingUtil {

    private int page;
    private int totalElements;  // 전체 데이터 수
    private int totalPages;
    private int startPage;
    private int endPage;
    private boolean isPrev;
    private boolean isNext;

    public PagingUtil(int totalElements, PagingDTO pagingDto) {
        this.totalElements = totalElements;
        this.calculation(pagingDto);
    }


    private void calculation(PagingDTO pagingDto) {

        // 전체 페이지 수 계산
        totalPages = ((totalElements - 1) / pagingDto.getSize()) + 1;

        // 현재 페이지 번호가 전체 페이지 수보다 큰 경우, 현재 페이지 번호에 전체 페이지 수 저장
        if (pagingDto.getPage() > totalPages) {
            pagingDto.setPage(totalPages);
        }

        // 하단에 보여줄 첫 페이지 번호 계산
        startPage = ((pagingDto.getPage() - 1) / pagingDto.getPageNum()) * pagingDto.getPageNum() + 1;

        // 하단에 보여줄 끝 페이지 번호 계산
        endPage = startPage + pagingDto.getPageNum() - 1;

        // 끝 페이지가 전체 페이지 수보다 큰 경우, 끝 페이지 전체 페이지 수 저장
        if (endPage > totalPages) {
            endPage = totalPages;
        }

        // 이전 페이지 존재 여부 확인
        isPrev = startPage != 1;

        // 다음 페이지 존재 여부 확인
        isNext = (endPage * pagingDto.getSize()) < totalElements;

        this.page = pagingDto.getPage();
    }
}
