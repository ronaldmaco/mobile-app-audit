import XCTest
@testable import App

final class ProfileStoreTests: XCTestCase {
    func testDecodesProfile() throws {
        let json = #"{"id":"u1","name":"Ana","avatar":"https://cdn.example.com/a/u1.jpg"}"#
        let profile = try JSONDecoder().decode(Profile.self, from: Data(json.utf8))
        XCTAssertEqual(profile.name, "Ana")
    }

    func testDecodesProfileWithoutPhoto() throws {
        let json = #"{"id":"u2","name":"Luis","avatar":""}"#
        let profile = try JSONDecoder().decode(Profile.self, from: Data(json.utf8))
        XCTAssertEqual(profile.avatar, "")
    }
}
