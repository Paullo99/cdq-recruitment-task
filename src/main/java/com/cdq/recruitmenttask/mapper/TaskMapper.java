package com.cdq.recruitmenttask.mapper;

import com.cdq.recruitmenttask.dto.TaskSummaryResponse;
import com.cdq.recruitmenttask.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "taskId", source = "id")
    TaskSummaryResponse toTaskSummaryResponse(Task task);
}