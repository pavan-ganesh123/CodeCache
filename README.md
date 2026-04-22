Just a NOTE

TRL Tranformers reinforcment learning
This uses Proximal policy optimization
Which is algorithm in reinforcement learning where model improves gradually without making unstable jumps
Updates the model but not too much at once

DPO (Direct Preference Optimization)

Instead of reinforcement methods ( Rewards and penalties ) This uses comparisions
Winning response and Losing response from Humans
and tries to give rest of responses like winning response
Gradio is Python library that instantly convert ML model into web application UI instantly
Where we can enter text and give images as input if needed

https://colab.research.google.com/drive/1hV6Gcz8vBRS9t0bYkBp6W1ne_yqG6mJx?usp=sharing#scrollTo=629dcca3


model/
  User.java

repository/
  UserRepository.java

service/
  AuthService.java   <-- login logic (verifyUser)
  UserService.java

controller/
  AuthResolver.java  <-- login mutation
  UserResolver.java

config/
  SecurityConfig.java
  JwtFilter.java
  JwtUtil.java

dto/   (you can create this)
  LoginRequest.java
  LoginResponse.java


In frontend
mutation {
  login(username: "john", password: "1234") {
    accessToken
    userId
  }
}


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}


@Component
public class JwtUtil {

    private String SECRET = "mysecretkey";

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 min
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
}

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String verifyUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getUsername());
    }
}


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String verifyUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getUsername());
    }
}


@Component
public class AuthResolver {

    @Autowired
    private AuthService authService;

    @MutationMapping
    public LoginResponse login(@Argument String username, @Argument String password) {
        String token = authService.verifyUser(username, password);

        return new LoginResponse(token);
    }
}

public class LoginResponse {
    private String accessToken;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}

=============================================================

Will be moving from sql to postgres 
I want to use kafka for chat communication between present and we will use json objects 
and we will use "Neon" as cloud database storage for free and try testing
