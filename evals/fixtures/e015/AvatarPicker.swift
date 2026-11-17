import PhotosUI
import UIKit

final class AvatarPicker: NSObject, PHPickerViewControllerDelegate {
    func present(from vc: UIViewController) {
        var config = PHPickerConfiguration()
        config.filter = .images
        config.selectionLimit = 1
        let picker = PHPickerViewController(configuration: config)
        picker.delegate = self
        vc.present(picker, animated: true)
    }

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)
        // load the selected image and upload it as the avatar
    }
}
