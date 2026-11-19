import SwiftUI

struct PaywallView: View {
    @EnvironmentObject var store: SubscriptionStore
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(spacing: 20) {
            Text("StoryTime Plus").font(.largeTitle.bold())
            Text("300+ stories, new ones every week, offline listening.")
            Text("Only \(store.product?.displayPrice ?? "$4.99")!")
                .font(.title2)
            Button("Start now") { Task { await store.buy(); dismiss() } }
                .buttonStyle(.borderedProminent)
            Button("Not now") { dismiss() }
        }
        .padding()
    }
}
