package site.metacoding.fileupload;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @GetMapping("/main")
    public String main(Model model) {
        User users = userRepository.findById(1).get();

        model.addAttribute("user", users);
        return "main";
    }

    @PostMapping("/join")
    public String join(JoinDto joinDto) { // 버퍼로 읽는거 1. json 2. 있는 그대로 받고 싶을 때

        UUID uuid = UUID.randomUUID(); // 범용 고유 식별자
        String requestFileName = joinDto.getFile().getOriginalFilename();
        // System.out.println("전송받은 파일명 : " + requestFileName);

        // 34938930_34u89048309_4u39i48903_a.png
        String imgurl = uuid + "_" + requestFileName;

        // 메모리에 있는 파일 데이터를 파일시스템으로 옮겨야 함.
        // 1. 빈 파일 생성 (haha.png)
        // File file = new File("d:\\example\\file.txt");
        // 2. 빈 파일에 스트림 연결
        // 3. for문 돌리면서 바이트로 쓰면 됨. FileWriter 객체!!

        try {
            // 1. 폴더가 이미 만들어져 있어야 함. (경로를 만들어주지 않기 때문)
            // 2. 리눅스 / 사용하고, 윈도우 \ 사용! (OS관점임. 프로그램에서 라이브러리로 지원할 수도 있어서 테스트 필수!)
            // 풀경로 : imgUrl = C:/upload/a.png
            // 3. 윈도우 : c:/upload/ 4. 리눅스 : /upload/
            // 우리는 상대경로 사용할 예정

            // jar 파일로 구우면 안 돌아감
            Path filePath = Paths.get("src/main/resources/static/upload/" + imgurl);
            // System.out.println(filePath);

            Files.write(filePath, joinDto.getFile().getBytes()); // (파일 경로, 이미지(바이트)) - 어느 경로에 어떤 파일 쓸건지

            userRepository.save(joinDto.toEntity(imgurl)); // DB에는 이미지 경로를 저장
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "joinComplete"; // ViewResolver
    }
}
