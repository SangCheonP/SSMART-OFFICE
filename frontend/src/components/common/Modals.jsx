import useModalStore from "@/store/useModalStore";

const Modals = () => {
  const { modals, closeModal } = useModalStore();

  return modals.map((modal, index) => {
    const { Component, props } = modal;
    const { onSubmit, ...restProps } = props;

    const onClose = () => {
      closeModal(Component);
    };

    const handleSubmit = async () => {
      if (typeof onSubmit === "function") {
        await onSubmit();
      }
      onClose();
    };

    return (
      <Component
        key={index}
        onClose={onClose}
        onSubmit={handleSubmit}
        {...restProps}
      />
    );
  });
};

export default Modals;
