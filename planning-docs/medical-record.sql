CREATE TABLE [users] (
  [id] bigint PRIMARY KEY IDENTITY(1, 1),
  [username] nvarchar(255) UNIQUE NOT NULL,
  [password] nvarchar(255) NOT NULL,
  [role] nvarchar(255) NOT NULL
)
GO

CREATE TABLE [specialties] (
  [id] bigint PRIMARY KEY IDENTITY(1, 1),
  [name] nvarchar(255) UNIQUE NOT NULL
)
GO

CREATE TABLE [doctors] (
  [id] bigint PRIMARY KEY IDENTITY(1, 1),
  [uin] nvarchar(255) UNIQUE NOT NULL,
  [name] nvarchar(255) NOT NULL,
  [specialty_id] bigint NOT NULL,
  [is_gp] boolean NOT NULL DEFAULT (false),
  [user_id] bigint UNIQUE
)
GO

CREATE TABLE [diagnoses] (
  [id] bigint PRIMARY KEY IDENTITY(1, 1),
  [code] nvarchar(255) UNIQUE NOT NULL,
  [name] nvarchar(255) NOT NULL
)
GO

CREATE TABLE [patients] (
  [id] bigint PRIMARY KEY IDENTITY(1, 1),
  [egn] nvarchar(255) UNIQUE NOT NULL,
  [name] nvarchar(255) NOT NULL,
  [gp_id] bigint,
  [is_insured] boolean NOT NULL DEFAULT (true),
  [user_id] bigint UNIQUE
)
GO

CREATE TABLE [examinations] (
  [id] bigint PRIMARY KEY IDENTITY(1, 1),
  [exam_date] datetime NOT NULL,
  [doctor_id] bigint NOT NULL,
  [patient_id] bigint NOT NULL,
  [diagnosis_id] bigint,
  [treatment] text,
  [price] decimal(10,2) NOT NULL,
  [paid_by_nzok] boolean NOT NULL
)
GO

CREATE TABLE [sick_leaves] (
  [id] bigint PRIMARY KEY IDENTITY(1, 1),
  [start_date] date NOT NULL,
  [duration_days] int NOT NULL,
  [examination_id] bigint UNIQUE NOT NULL
)
GO

CREATE TABLE [specialty_diagnoses] (
  [specialty_id] bigint NOT NULL,
  [diagnosis_id] bigint NOT NULL,
  PRIMARY KEY ([specialty_id], [diagnosis_id])
)
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'ADMIN, DOCTOR, PATIENT',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'users',
@level2type = N'Column', @level2name = 'role';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Напр. Кардиология, Педиатрия, Обща медицина',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'specialties',
@level2type = N'Column', @level2name = 'name';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Връзка към конкретна специалност',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'doctors',
@level2type = N'Column', @level2name = 'specialty_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'ЕГН',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'patients',
@level2type = N'Column', @level2name = 'egn';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Избран личен лекар',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'patients',
@level2type = N'Column', @level2name = 'gp_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Здравноосигурителен статус за последните 6 месеца',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'patients',
@level2type = N'Column', @level2name = 'is_insured';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Връзка към потребителския профил за вход',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'patients',
@level2type = N'Column', @level2name = 'user_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Може да е NULL, ако пациентът не е болен (Is sick? -> No)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'examinations',
@level2type = N'Column', @level2name = 'diagnosis_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Назначено лечение (опционално)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'examinations',
@level2type = N'Column', @level2name = 'treatment';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Цена, определена от лекаря',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'examinations',
@level2type = N'Column', @level2name = 'price';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'True = НЗОК плаща, False = Пациентът плаща',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'examinations',
@level2type = N'Column', @level2name = 'paid_by_nzok';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'Всеки преглед може да има най-много един болничен',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'sick_leaves',
@level2type = N'Column', @level2name = 'examination_id';
GO

ALTER TABLE [doctors] ADD FOREIGN KEY ([user_id]) REFERENCES [users] ([id])
GO

ALTER TABLE [patients] ADD FOREIGN KEY ([user_id]) REFERENCES [users] ([id])
GO

ALTER TABLE [patients] ADD FOREIGN KEY ([gp_id]) REFERENCES [doctors] ([id])
GO

ALTER TABLE [examinations] ADD FOREIGN KEY ([doctor_id]) REFERENCES [doctors] ([id])
GO

ALTER TABLE [examinations] ADD FOREIGN KEY ([patient_id]) REFERENCES [patients] ([id])
GO

ALTER TABLE [examinations] ADD FOREIGN KEY ([diagnosis_id]) REFERENCES [diagnoses] ([id])
GO

ALTER TABLE [sick_leaves] ADD FOREIGN KEY ([examination_id]) REFERENCES [examinations] ([id])
GO

ALTER TABLE [doctors] ADD FOREIGN KEY ([specialty_id]) REFERENCES [specialties] ([id])
GO

ALTER TABLE [specialty_diagnoses] ADD FOREIGN KEY ([specialty_id]) REFERENCES [specialties] ([id])
GO

ALTER TABLE [specialty_diagnoses] ADD FOREIGN KEY ([diagnosis_id]) REFERENCES [diagnoses] ([id])
GO
