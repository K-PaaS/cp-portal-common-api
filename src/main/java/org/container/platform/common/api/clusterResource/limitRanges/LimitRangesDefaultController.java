package org.container.platform.common.api.clusterResource.limitRanges;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * LimitRanges Default Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.26
 **/
@Tag(name = "LimitRangesDefaultController v1")
@RestController
@RequestMapping(value = "/limitRanges")
public class LimitRangesDefaultController {
    private final LimitRangesDefaultService limitRangesDefaultService;

    /**
     * Instantiates a LimitRangesDefault Controller
     *
     * @param limitRangesDefaultService the limitRangesDefault Service
     */
    @Autowired
    public LimitRangesDefaultController(LimitRangesDefaultService limitRangesDefaultService) {
        this.limitRangesDefaultService = limitRangesDefaultService;
    }


    /**
     * LimitRangesDefault 목록 조회(Get LimitRangesDefault list)
     *
     * @return the limitRangesDefault list
     */
    @Operation(summary = "LimitRangesDefault 목록 조회(Get LimitRangesDefault list)", operationId = "getLrDefaultList")
    @GetMapping
    public LimitRangesDefaultList getLrDefaultList() {
        return limitRangesDefaultService.getLrDefaultList();
    }
}
