# ChefOnCall

Java Swing frontend project for booking private chefs.

## Run in VS Code

1. Extract the ZIP.
2. Open the **ChefOnCall** folder in VS Code.
3. Open `src/Main.java`.
4. Click **Run** above `main()`.

Or use the terminal:

```powershell
javac -d out src/*.java
java -cp out Main
```

## Sign in

Create an account first using:
- Full name: first name + last name
- Email: valid email with a recognised extension such as `.com`, `.in`, `.org`, `.net`
- Phone: exactly 10 digits
- Password: exactly 6 digits, numbers only

The Sign In page now validates fields while signing in, supports Enter-to-submit, shows clear error messages, and includes an eye button to show/hide the password.

## Note

This is a frontend-only Java Swing demo. Payment is simulated and no real transaction is made.
