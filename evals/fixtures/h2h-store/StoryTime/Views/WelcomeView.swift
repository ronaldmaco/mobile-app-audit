import SwiftUI

struct WelcomeView: View {
    @EnvironmentObject var session: SessionStore
    @State private var email = ""
    @State private var password = ""
    @State private var childName = ""
    @State private var code = ""
    @State private var awaitingCode = false
    @State private var creating = false

    var body: some View {
        Form {
            TextField("Email", text: $email).textInputAutocapitalization(.never)
            SecureField("Password", text: $password)
            if creating {
                TextField("Your child's first name", text: $childName)
                Button("Create account") { Task { try? await session.signUp(email: email, password: password, childName: childName) } }
            } else if awaitingCode {
                TextField("Code from SMS", text: $code).keyboardType(.numberPad)
                Button("Verify") { Task { try? await session.verify(code: code) } }
            } else {
                Button("Sign in") { Task { try? await session.requestCode(email: email, password: password); awaitingCode = true } }
                Button("New here? Create an account") { creating = true }
            }
        }
    }
}
