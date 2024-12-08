package app.project.operationgreenergit.controller;

import app.project.operationgreenergit.service.GitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class GitController {

	private final GitService service;

	@PostMapping(
			path = "/generate-commit-history"
	)
	public ResponseEntity<String> generateCommitHistory() {
		service.generateCommitHistory();
		return ResponseEntity.ok("success");
	}

}
