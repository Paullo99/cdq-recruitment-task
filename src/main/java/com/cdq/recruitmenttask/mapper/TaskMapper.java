package com.cdq.recruitmenttask.mapper;

import com.cdq.recruitmenttask.dto.TaskSummaryResponse;
import com.cdq.recruitmenttask.model.Task;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskSummaryResponse toTaskSummaryResponse(Task task);
}