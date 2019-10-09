export interface IQA {
    id?: number;
    question?: string;
    answer?: string;
    order?: number;
}

export const defaultValue: Readonly<IQA> = {};
