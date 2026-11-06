protocol KeyboardAvoiding: AnyObject { var formScrollView: UIScrollView! { get }; var originalContentSize: CGSize? { get set } }
extension KeyboardAvoiding {
    func keyboardWillShow(_ n: Notification) {
        originalContentSize = formScrollView.contentSize
        formScrollView.contentSize.height += keyboardHeight(n)
    }
    func keyboardWillHide(_ n: Notification) {
        formScrollView.contentSize = originalContentSize!
        originalContentSize = nil
    }
}
