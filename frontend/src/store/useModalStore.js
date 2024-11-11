import { create } from "zustand";

const useModalStore = create((set) => ({
  modals: [],
  openModal: (Component, props) =>
    set((state) => {
      const updateModals = [...state.modals, { Component, props }];
      return { modals: updateModals };
    }),
  closeModal: (Component) =>
    set((state) => ({
      modals: state.modals.filter((modal) => modal.Component !== Component),
    })),
}));

export default useModalStore;
