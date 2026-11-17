import SwiftUI

struct LoginView: View {
    @StateObject var model = LoginModel()

    var body: some View {
        VStack(spacing: 16) {
            TextField("Email", text: $model.email)
            SecureField("Password", text: $model.password)
            Button("Log in") { model.loginWithEmail() }
            Divider()
            // Third-party login used to create and sign in to the main account.
            Button("Continue with Google") { model.loginWithGoogle() }
        }
    }
}
