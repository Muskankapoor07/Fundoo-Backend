package com.fundoonotes.fundoo_notes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class NoteDTO {

    private String title;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String color;

    private LocalDateTime reminder;
}
